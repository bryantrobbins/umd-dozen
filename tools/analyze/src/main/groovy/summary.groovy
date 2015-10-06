// HOST, PORT, DB_ID, SUITE_ID, BUNDLE_LIST

import com.mongodb.MongoClient
import com.mongodb.DB
import com.mongodb.BasicDBObject

String host = args[0]
int port = Integer.parseInt(args[1])
String dbId = args[2]
String suiteId = args[3]
println args

// Basic connection
MongoClient mongoClient = new MongoClient(host, port)
DB db = mongoClient.getDB(dbId);
println "Connected to DB ${db.getName()}"

// Sanity check suite collection
def testCount = db.getCollection("suite_${suiteId}").count()
println "${testCount} test cases in suite ${suiteId}"

// Sanity check bundles
List bundleIds = db.getCollection("results").findOne().get("bundleId")
println "Results from bundles ${bundleIds}"
checkBundles(db, bundleIds)

// Get result counts by category
int passCount = getResultCountInClass(db, "passingResults")
int failCount = getResultCountInClass(db, "failingResults")
int badCount = getResultCountInClass(db, "inconsistentResults")
println "Test results summary: ${passCount} pass, ${failCount} fail, ${badCount} inconsistent"

println "First 5 inconsistent results"
getFirstFiveInClass(db, "inconsistentResults")

def checkBundles(DB db, def bundleIds) {
	def verifySets = []
	bundleIds.each {
		verifySets << getSets(db, it)
	}

	assert([] == (verifySets[0]['test'] - verifySets[1]['test']))
	assert([] == (verifySets[0]['test'] - verifySets[2]['test']))
	assert([] == (verifySets[2]['test'] - verifySets[1]['test']))
}

def getSets(DB db, String bundleId) {
	def ret = [:]
	ret['test'] = []
	ret['exec'] = []
	def execs = db.getCollection("bundle_${bundleId}").find()
	execs.each {
		ret['test'].add(it.testId)
		ret['exec'].add(it.executionId)
	}

	ret
}

int getResultCountInClass(DB db, String cl) {
	db.getCollection("results").findOne().get("results").get(cl).size()
}

def getFirstFiveInClass(DB db, String cl) {
	def arr = db.getCollection("results").findOne().get("results").get(cl)
	1.upto(5) {
		println arr[it]	
	}
}

