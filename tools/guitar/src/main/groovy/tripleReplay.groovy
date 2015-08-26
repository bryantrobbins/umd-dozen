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
        
// Use Jenkins client to launch jobs (we want 3 runs with same params)
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)
jobParams.put("BUNDLE_ID", bundleIds[1])
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)
jobParams.put("BUNDLE_ID", bundleIds[2])
jenkinsClient.submitJob("replay-suite", jobParams)
sleep(500)

// Now for the results job
def resultsParams = [:]
resultsParams.put("DB_ID", args[1])
resultsParams.put("SUITE_ID", args[2])
resultsParams.put("BUNDLE_IDS", "${bundleIds[0]},${bundleIds[1]},${bundleIds[2]}")
jenkinsClient.submitJob("post-results", resultsParams)
