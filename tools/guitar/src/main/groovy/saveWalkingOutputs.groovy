// ARGS: HOST, PORT, DB_ID, EXEC_ID, COVERAGE_DIR

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import edu.umd.cs.guitar.processors.guitar.MapProcessor
import edu.umd.cs.guitar.processors.guitar.TestcaseProcessor
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import groovy.io.FileType

// Grab args
println args
host=args[0]
port=args[1]
dbId=args[2]
testId=args[3]

// Create a TestDataManager
def manager = new TestDataManager(host, port, dbId)

// Prepare processors
ArtifactCategory cat = ArtifactCategory.TEST_INPUT
LogProcessor lp = new LogProcessor()
MapProcessor mp = new MapProcessor(manager.getDb())
TestcaseProcessor tp = new TestcaseProcessor()

// Save Log
println "Saving log ${findLogFile()}"
def opts = [(LogProcessor.FILE_PATH_OPTION): findLogFile() ]
manager.saveArtifact(cat, lp, opts, testId)

// Save Map
println "Saving WalkingRipper.MAP"
opts = [(MapProcessor.FILE_PATH_OPTION): "WalkingRipper.MAP"]
manager.saveArtifact(cat, mp, opts, testId)

// Save Testcase
println "Saving WalkingRipper.TST"
opts = [(TestcaseProcessor.FILE_PATH_OPTION): "WalkingRipper.TST"]
manager.saveArtifact(cat, tp, opts, testId)

String findLogFile(){
  def found = []
  new File('.').eachFileMatch(FileType.ANY, ~/.*\.log/) {
    found << it.name
  }
  found[0]
}
