// HOST, PORT, DB_ID, SUITE_ID, BUNDLE_LIST

import com.mongodb.MongoClient
import com.mongodb.DB
import com.mongodb.BasicDBObject

String host = args[0]
int port = Integer.parseInt(args[1])
String dbId = args[2]
String suiteId = args[3]
String resultId = args[4]
println args

// Basic connection
MongoClient mongoClient = new MongoClient(host, port)
DB db = mongoClient.getDB(dbId);
println "Connected to DB ${db.getName()}"
println "Working with result ID ${resultId}"

def getResultsObj(db, resultId) {
	getResultsParentObj(db, resultId).get("results")
}

def getResultsParentObj(db, resultId) {
	BasicDBObject query = new BasicDBObject('resultId', resultId)
	db.getCollection("results").findOne(query)
}

// Sanity check suite collection
def suiteName = "suite_${suiteId}"
println suiteName
def coll = db.getCollection(suiteName)
println coll
def testCount = coll.find().size()
println "${testCount} test cases in suite ${suiteId}"

// Sanity check bundles
List bundleIds = getResultsParentObj(db, resultId).get("bundleId")
println "Results from bundles ${bundleIds}"
checkBundles(db, bundleIds)

// Get result counts by category
int passCount = getResultCountInClass(db, "passingResults", resultId)
int failCount = getResultCountInClass(db, "failingResults", resultId)
int badCount = getResultCountInClass(db, "inconsistentResults", resultId)
println "Test results summary: ${passCount} pass, ${failCount} fail, ${badCount} inconsistent"

println "First 5 inconsistent results"
getFirstFiveInClass(db, "inconsistentResults", resultId)

def checkBundles(DB db, def bundleIds) {
	def verifySets = []
	bundleIds.each {
		verifySets << getSets(db, it)
	}

	//println (verifySets[0]['test'] - verifySets[1]['test'])
	//assert([] == (verifySets[0]['test'] - verifySets[2]['test']))
	//assert([] == (verifySets[2]['test'] - verifySets[1]['test']))
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

int getResultCountInClass(DB db, String cl, String resultId) {
	getResultsObj(db, resultId).get(cl).size()
}

def getFirstFiveInClass(DB db, String cl, String resultId) {
	def arr = getResultsObj(db, resultId).get(cl)
	1.upto(5) {
		println arr[it]	
	}
}

