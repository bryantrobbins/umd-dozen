// HOST, PORT, DB_ID, RESULT_ID, TEST_ID

import com.mongodb.MongoClient
import com.mongodb.DB
import com.mongodb.BasicDBObject
import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import edu.umd.cs.guitar.processors.guitar.*
import edu.umd.cs.guitar.processors.applog.TextObject
import edu.umd.cs.guitar.model.data.*

String host = args[0]
String portString = args[1]
int port = Integer.parseInt(args[1])
String dbId = args[2]
String resultId = args[3]
String testId = args[4]
println args

TestDataManager testDataManager = new TestDataManager(host, portString, dbId)
GUIProcessor guiProcessor = new GUIProcessor(testDataManager.getDb())
EFGProcessor efgProcessor = new EFGProcessor(testDataManager.getDb())
MapProcessor mapProcessor = new MapProcessor(testDataManager.getDb())
TestcaseProcessor testcaseProcessor = new TestcaseProcessor()
LogProcessor logProcessor = new LogProcessor()
println "Connected to TDM instance"

MongoClient mongoClient = new MongoClient(host, port)
DB db = mongoClient.getDB(dbId);
println "Connected directly to DB ${db.getName()}"

// Get results Map and sanity check for resultId
BasicDBObject resultQuery = new BasicDBObject("resultId", resultId)
BasicDBObject resultsObject = db.getCollection("results").findOne(resultQuery)
assert resultId == resultsObject.resultId

// Get associated suiteId
String suiteId = resultsObject.suiteId

// For each bundle
resultsObject.bundleId.each {
	assert null != it
	BasicDBObject execQuery = new BasicDBObject("testId", testId)

	// Get execId
	String execId = db.getCollection("bundle_${it}").findOne(execQuery).executionId
	assert null != execId
	println "Using execution id ${execId} from bundle ${it}"

	// Get execId's artifact objects from db (should just be logs)
	BasicDBObject artifactQuery = new BasicDBObject("ownerId", execId)
	db.getCollection("artifacts").find(artifactQuery).each {
		println it
	}
}

// Get GUI Structure
//GUIStructure gui = (GUIStructure) testDataManager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT, suiteId, guiProcessor)
//assert null != gui
//println "Got GUIStructure"

// Get EFG
//EFG efg = (EFG) testDataManager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT, suiteId, efgProcessor)
//assert null != efg
//println "Got EFG"

// Get test case
//TestCase tc = (TestCase) testDataManager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_INPUT, testId, testcaseProcessor)
//assert null != tc
//println "Got TestCase"

// Get log file
//TextObject log = (TextObject) testDataManager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT, execId, logProcessor)
//assert null != log
//println "Got TextObject"

//BasicDBObject artifactObject = db.getCollection("artifacts").find(artifactQuery)
