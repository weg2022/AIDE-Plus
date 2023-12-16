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
import android.text.TextUtils;

public class AndroidManifestParser {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    public Node manifestNode;

    public AndroidManifestParser(String xmlPath) {
        try {
			loadData(new File(xmlPath));
		} catch (Exception e) {} 
	}
    protected void loadData(File xmlPath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlPath);

        NodeList manifestNodeList = doc.getElementsByTagName("manifest");
        //一定只有一个manifest标签
		manifestNode = manifestNodeList.item(0);
    }

    public int getMiniSdk() {
        int sdk = -1;
        NodeList list = manifestNode.getChildNodes();
        for (int index = 0; index < list.getLength(); index ++) {
            if ("uses-sdk".equals(list.item(index).getNodeName())) {
                String value = getNodeValue(list.item(index), "android:minSdkVersion");
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
        NodeList list = manifestNode.getChildNodes();
        for (int index = 0; index < list.getLength(); index ++) {
            if ("uses-sdk".equals(list.item(index).getNodeName())) {
                String value = getNodeValue(list.item(index), "android:targetSdkVersion");
				try {
					sdk = Integer.parseInt(value);
				} catch (NumberFormatException e) {

				}
				break;
            }
        }
        return sdk;
    }
	public boolean getAndroidFxtractNativeLibs() {
        boolean extractNativeLibs = true;
        NodeList childNodes = manifestNode.getChildNodes();
        for (int index = 0; index < childNodes.getLength(); index++) {
            if ("application".equals(childNodes.item(index).getNodeName())) {
                String value = getNodeValue(childNodes.item(index), "android:extractNativeLibs");
				if ("false".equals(value)) {
					extractNativeLibs = false;
				} else {
					extractNativeLibs = true;
				}
				break;
            }
        }
        return extractNativeLibs;
    }
    public static String getNodeValue(Node n, String attr) {
        if (n == null) return "";

        NamedNodeMap attributesMap = n.getAttributes();
        Node nn;
		if (attributesMap == null || (nn = attributesMap.getNamedItem(attr)) == null) {
            return "";
		}
		return nn.getNodeValue();
    }
}



