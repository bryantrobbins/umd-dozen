import groovyx.net.http.RESTClient
 
def client = new RESTClient('http://guitar05.cs.umd.edu:8888' )

println getAwaitingBuildCount(client)

int getAwaitingBuildCount(RESTClient client){
	def resp = client.get( path : '/queue/api/json' )
	return resp.data['items'].size()
}


