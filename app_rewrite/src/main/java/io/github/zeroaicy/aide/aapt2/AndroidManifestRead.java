package io.github.zeroaicy.aide.aapt2;

import java.io.File;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AndroidManifestRead {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    public Node manifest;
    
    public AndroidManifestRead(String xmlPath) {
        try {
			loadData(new File(xmlPath));
		} catch (Exception e) {

		} 
	}
    protected void loadData(File xmlPath) throws ParserConfigurationException, SAXException, IOException {
        
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlPath);
        NodeList temp = doc.getElementsByTagName("manifest");
        manifest = temp.item(0);
    }
    public int getMiniSdk() {
        int sdk = -1;
        NodeList list = manifest.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1) {
            if ("uses-sdk".equals(list.item(i).getNodeName())) {
                String value = getNodeValue(list.item(i), "android:minSdkVersion");
				try {
					
					
					sdk = Integer.parseInt(value);
				} catch (NumberFormatException e) {

				}
				break;
				
            }
        }
        return sdk;
    }
	public int getTargetSdk() {
        int sdk = -1;
        NodeList list = manifest.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1) {
            if ("uses-sdk".equals(list.item(i).getNodeName())) {
                String value = getNodeValue(list.item(i), "android:targetSdkVersion");
				try {
					sdk = Integer.parseInt(value);
				} catch (NumberFormatException e) {

				}
				break;
				
            }
        }
        return sdk;
    }

    public static String getNodeValue(Node n, String attr) {
        if (n == null) return "";
        NamedNodeMap map = n.getAttributes();
        if (map == null)
            return "";
        Node nn = map.getNamedItem(attr);
        if (nn != null)
            return nn.getNodeValue();
        return "";
    }
}



