import edu.umd.cs.guitar.main.ExperimentManager
import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.main.TestDataManagerKeys
import edu.umd.cs.guitar.main.TestDataManagerCollections
import edu.umd.cs.guitar.processors.guitar.FeaturesProcessor

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import edu.umd.cs.guitar.artifacts.ArtifactCategory;
import edu.umd.cs.guitar.processors.features.FeaturesObject;
import edu.umd.cs.guitar.processors.guitar.FeaturesProcessor;
import edu.umd.cs.guitar.util.MongoUtils;

String mongoHost = args[0]
String mongoPort = args[1]
String dbId = args[2]
String suiteId = args[3]
int maxN = 4
println args

// Save some properties of this execution
Properties groupIds = new Properties();

// Bringing in the feature-posting code
TestDataManager manager = new TestDataManager(mongoHost, mongoPort, dbId);
FeaturesProcessor fproc = new FeaturesProcessor(manager, maxN);
HashSet<String> allFeatures = new HashSet<String>();

int count = 0;
String groupId = manager.generateId();

// For each test in the suite
for (String testId : manager.getTestIdsInSuite(suiteId)) {
  count++;
  if ((count % 100) == 0) {
    System.out.println(".");
  }

  // Create features object and add to DB as test output artifact
  artifactId = addFeaturesToTest(manager, testId, suiteId, fproc, false);

  // Fetch new features object
  fob = (FeaturesObject) manager.getArtifactById(artifactId, fproc);

  // Add features to allFeatures set
  allFeatures.addAll(fob.getFeatures());
}

// Build the DBObject
BasicDBObject bdo = new BasicDBObject();

// Add GroupId
bdo.put(TestDataManagerKeys.GROUP_ID, groupId);

// Add suite ids
bdo.put(TestDataManagerKeys.SUITE_ID, suiteId);

// Build and add the global feature list
BasicDBList dbl = new BasicDBList();
System.out.println("Features List has size of " + allFeatures.size());
dbl.addAll(allFeatures);
bdo.put(TestDataManagerKeys.FEATURES_LIST, dbl);

// Add the value of N
bdo.put(TestDataManagerKeys.MAX_N, maxN);

// Record the group entry
MongoUtils.addItemToCollection(manager.getDb(),
  TestDataManagerCollections.GROUPS,
  bdo);

groupIds.setProperty("groupId", groupId)
groupIds.store(new FileOutputStream("groups.properties"), null);

private static String addFeaturesToTest(
  final TestDataManager manager,
  final String testId,
  final String suiteId,
  final FeaturesProcessor featuresProcessor,
  final boolean trim) {

  // Convert trim arg to String
  String trimString = "false";
  if (trim) {
    trimString = "true";
  }

  // Build options
  Map<String, String> options = new HashMap<String, String>();
  options.put(FeaturesProcessor.TEST_ID_OPTION, testId);
  options.put(FeaturesProcessor.SUITE_ID_OPTION, suiteId);
  options.put(FeaturesProcessor.TRIM_OPTION, trimString);
  options.put(FeaturesProcessor.REMOVE_OPTION, "true");

  // Save features object
  return manager.saveArtifact(
    ArtifactCategory.TEST_INPUT,
    featuresProcessor,
    options,
    testId
  );
}
