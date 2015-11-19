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
 
int ntests=Integer.parseInt(args[3])
println "Ripping ${ntests} tests"

int count = 0
boolean useShorterMin = false
int BUILD_COUNT_THRESHOLD = 5

while (count < ntests) {
				count++

        // build Map of params
        // I have only used text params, but perhaps others supported via Jenkins Remote API
        def jobParams = new HashMap<String, String>();
        jobParams.put("AUT_NAME", args[0])
        jobParams.put("DB_ID", args[1])
        jobParams.put("TESTCASE_LENGTH", args[2])
        
        // Use Jenkins client to launch job
        jenkinsClient.submitJob("rip-testcase", jobParams)

				// Always sleep a tiny bit
				// We don't want to do this once we are in the holding pattern
				if(useShorterMin) {
					sleep(1000)
				} else {
					sleep(10000)
				}
	
				// sleep more if necessary to let the master catch up
				while(getAwaitingBuildCount(client) > BUILD_COUNT_THRESHOLD) {
					useShorterMin = true
					sleep(1000)
				}
}

int getAwaitingBuildCount(RESTClient client){
  def resp = client.get( path : '/queue/api/json' )
  return resp.data['items'].size()
}
