import com.mongodb.MongoClient
import com.mongodb.DB
import com.mongodb.BasicDBObject
import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import edu.umd.cs.guitar.processors.guitar.*
import edu.umd.cs.guitar.processors.applog.TextObject
import edu.umd.cs.guitar.model.data.*
import edu.umd.cs.guitar.model.IO

String host = args[0]
String portString = args[1]
int port = Integer.parseInt(args[1])
String dbId = args[2]
String suiteId = args[3]
println args

TestDataManager testDataManager = new TestDataManager(host, portString, dbId)
MapProcessor mapProcessor = new MapProcessor(testDataManager.getDb())
println "Connected to TDM instance"

def gui = inspectGUI(testDataManager, suiteId)
inspectEFG(testDataManager, gui, suiteId)

def inspectGUI(TestDataManager manager, String suiteId) {
  GUIProcessor guiProcessor = new GUIProcessor(manager.getDb())
  GUIStructure gui = (GUIStructure) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT, suiteId, guiProcessor)
  assert null != gui
  println "Got GUIStructure"
  IO.writeObjToFile(gui, new FileOutputStream("gui.xml"))
  println "Wrote GUIStructure to gui.xml"

  gui
}

def inspectEFG(TestDataManager manager, GUIStructure gui, String suiteId) {
  EFGProcessor efgProcessor = new EFGProcessor(manager.getDb())
  EFG efg = (EFG) manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.SUITE_INPUT, suiteId, efgProcessor)
  assert null != efg
  println "Got EFG"
  IO.writeObjToFile(efg, new FileOutputStream("efg.xml"))
  println "Wrote EFG to efg.xml"

  // Dig in
  for(def event : efg.getEvents().getEvent()){
    println event.getType()
    println getWidgetFromEventId(gui, event.getEventId())
  }
}

def isWidget(ComponentType ct) {
  !(ct instanceof ContainerType)
}

String getId(ComponentType ct) {
  for(PropertyType pt : ct.getAttributes().getProperty()) {
    if(pt.getName().equals("ID")) {
      return pt.getValue()
    }
  }
  throw new RuntimeException("Could not find ID component")
}

def getWidgetFromWidgetIdInner(ComponentType ct, String widgetId) {
  if(isWidget(ct)) {
    String wid = getId(ct)
    if(getId(ct).equals(widgetId)) {
      println "Found the widget we are looing for"
      return ct
    } 
  } else {
    for(ComponentType ctInner : ct.getContents().getWidgetOrContainer()) {
      ComponentType res = getWidgetFromWidgetIdInner(ctInner, widgetId)
      if(res != null) {
        return res
      }
    }
  }
  return null
}

def getWidgetFromEventId(GUIStructure gui, String eventId) {
  String widgetId = "[${eventId.replaceAll("e", "w")}]"
  for(GUIType gt : gui.getGUI()) {
    ComponentType rootContainer = gt.getContainer()
    def res = getWidgetFromWidgetIdInner(rootContainer, widgetId)
    if(res != null) {
      return res
    }
  }

  println "Could not find widget ${widgetId} in GUIStructure"
  return null
}
