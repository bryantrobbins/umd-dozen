// ARGS: HOST, PORT, DB_ID, EXEC_ID, COVERAGE_DIR

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import edu.umd.cs.guitar.processors.guitar.MapProcessor
import edu.umd.cs.guitar.processors.guitar.GUIProcessor
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import groovy.io.FileType

// Grab args
println args
host=args[0]
port=args[1]
dbId=args[2]
execId=args[3]

// Create a TestDataManager
def manager = new TestDataManager(host, port, dbId)

// Prepare processors
ArtifactCategory cat = ArtifactCategory.TEST_INPUT
LogProcessor lp = new LogProcessor()
MapProcessor mp = new MapProcessor()
GUIProcessor gp = new GUIProcessor()

// Save Log
def opts = [(LogProcessor.FILE_PATH_OPTION): findLogFile() ]
manager.saveArtifact(cat, lp, opts, execId)

// Save Map
def opts = [(MapProcessor.FILE_PATH_OPTION): "WalkingRipper.MAP"]
manager.saveArtifact(cat, mp, opts, execId)

// Save GUI
def opts = [(GUIProcessor.FILE_PATH_OPTION): "WalkingRipper.GUI"]
manager.saveArtifact(cat, gp, opts, execId)

String findLogFile(){
  def found = []
  new File('.').eachFileMatch(FileType.ANY, ~/.*\.log/) {
    found << it.name
  }
  found[0]
}
