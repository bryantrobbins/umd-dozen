// ARGS: AUT_NAME, DB_ID, SUITE_ID, BUNDLE_ID

import edu.umd.cs.guitar.util.JenkinsClient
import edu.umd.cs.guitar.main.TestDataManager
import groovyx.net.http.RESTClient

def master = "guitar05.cs.umd.edu"

// Jenkins client
def jenkinsClient = new JenkinsClient(master, "8888", "", "admin", "amalga84go")

// TestDataManager
def manager = new TestDataManager(master, "37017", args[1])

// REST client to talk to Jenkins for build count
def client = new RESTClient("http://${master}:8888")

String bundlePrefix = args[3]
def bundleIds = ["${bundlePrefix}_1","${bundlePrefix}_2", "${bundlePrefix}_3"]

def jobParams = [:]
jobParams.put("AUT_NAME", args[0])
jobParams.put("DB_ID", args[1])
jobParams.put("SUITE_ID", args[2])
jobParams.put("BUNDLE_ID", bundleIds[0])
        
// Use Jenkins client to launch jobs (we want 3 runs with same params, except for bundle id)
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)
jobParams.put("BUNDLE_ID", bundleIds[1])
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)
jobParams.put("BUNDLE_ID", bundleIds[2])
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)

// Wait for replay-suite jobs to be complete
waitForBuildsComplete(client, "replay-suite")

// Wait for individual replay jobs to be complete
waitForBuildsComplete(client, "replay-test")

// Now for the results job
def resultsParams = [:]
resultsParams.put("DB_ID", args[1])
resultsParams.put("SUITE_ID", args[2])
resultsParams.put("BUNDLE_IDS", "${bundleIds[0]},${bundleIds[1]},${bundleIds[2]}")
jenkinsClient.submitJob("post-results", resultsParams)

// Wait for results job to be complete
waitForBuildsComplete(client, "post-results")

void waitForBuildsComplete(RESTClient client, String buildName) {
  // Wait for no jobs to be waiting
  int waiting = getAwaitingBuildCount(client, buildName)
  while(waiting > 0 ) {
    println "Still ${waiting} ${buildName} jobs waiting to start"
    Thread.sleep(300000)
    waiting = getAwaitingBuildCount(client, buildName)
  }

  // Wait for no jobs to be in progress
  int inprog = getInProgressBuildCount(client, buildName)
  while(inprog > 0 ) {
    println "Still ${inprog} ${buildName} jobs in progress"
    Thread.sleep(300000)
    waiting = getInProgressBuildCount(client, buildName)
  }
}

int getAwaitingBuildCount(RESTClient client, String buildName){
  def resp = client.get( path : '/queue/api/json' )
  return resp.data['items'].findAll {
    it.name.equals(buildName)
  }.size()
}

int getInProgressBuildCount(RESTClient client, String buildName){
  def resp = client.get( path : "/job/${buildName}/api/json")
  int count = 0
  for (def build : resp.data['builds']){
    def respBuild = client.get( path : "/job/${buildName}/${build.id}/api/json")
    if(respBuild.data['building'].equals("true")){
      count++
    }
  }

  return count
}
