package com.syndarin.erdi.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.net.Uri;
import android.util.Log;

import com.syndarin.erdi.entities.SaluteCategory;
import com.syndarin.erdi.entities.SaluteModel;
import com.syndarin.erdi.events.OnParserProgressEventsListener;

public class UpdateResponceParser implements Runnable {

	private final static String TAG="XML PARSER";
	
	private InputStream xmlResponce;
	
	private OnParserProgressEventsListener onParserProgressEventsListener;
	
	// ====================================================
	// CONSTRUCTORS
	// ====================================================
	
	public UpdateResponceParser(InputStream xmlResponce, OnParserProgressEventsListener onParserProgressEventsListener) {
		this.xmlResponce = xmlResponce;
		this.onParserProgressEventsListener=onParserProgressEventsListener;
		
	}

	// ====================================================
	// OVERRIDEN METHODS
	// ====================================================
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		ArrayList<SaluteCategory> categoriesArray=new ArrayList<SaluteCategory>();
		ArrayList<SaluteModel> modelsArray=new ArrayList<SaluteModel>();
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document dom=builder.parse(xmlResponce);
			
			Element root=dom.getDocumentElement();
			
			NodeList categoriesList=root.getElementsByTagName("razdel");
			
			int totalCategories=categoriesList.getLength();
			
			for(int i=0; i<totalCategories; i++){
				SaluteCategory category=new SaluteCategory();
				// getting <razdel> item
				Node categoryNode=categoriesList.item(i);
				// getting child elements
				NodeList categoryParams=categoryNode.getChildNodes();
				
				// getting <rid> parameter
				Node categoryRid=categoryParams.item(0);
				if(categoryRid.getNodeName().equals("rid")){
					int id=Integer.parseInt(categoryRid.getTextContent());
					category.setId(id);
					Log.i(TAG, "Category ID="+id+" parsed!");
				}
				// getting <title> parameter
				Node categoryTitle=categoryParams.item(1);
				if(categoryTitle.getNodeName().equals("title")){
					category.setTitle(categoryTitle.getTextContent());
					Log.i(TAG, "Category TITLE="+categoryTitle.getTextContent()+" parsed!");
				}
				// getting <desc> parameter of <razdel>
				Node categoryDescription=categoryParams.item(2);
				if(categoryDescription.getNodeName().equals("desc")){
					category.setDescription(categoryDescription.getTextContent());
					Log.i(TAG, "Category DESC="+categoryDescription.getTextContent()+" parsed!");
				}
				
				categoriesArray.add(category);
				
				Node categoryRows=categoryParams.item(3);
				if(categoryRows.getNodeName().equals("rows")){
					
					NodeList modelsRows=categoryRows.getChildNodes();
					
					int modelsCount=modelsRows.getLength();
					// iterating rows
					for(int j=0; j<modelsCount; j++){
						Node modelNode=modelsRows.item(j);
						NamedNodeMap attributesMap=modelNode.getAttributes();
						
						SaluteModel model=new SaluteModel();
						
						model.setParentId(category.getId());	
						
						
						Node md5PictureNode=attributesMap.getNamedItem("md5_picture");
						String md5PictureString=md5PictureNode.getTextContent();
						model.setPictureMD5(md5PictureString);
						Log.i(TAG, "MD5 picture ="+md5PictureString+" parsed!");
						
						Node md5PreviewNode=attributesMap.getNamedItem("md5_preview");
						String md5PreviewString=md5PreviewNode.getTextContent();
						model.setPreviewMD5(md5PreviewString);
						Log.i(TAG, "MD5 preview ="+md5PreviewString+" parsed!");
						
						Node md5VideoNode=attributesMap.getNamedItem("md5_video");
						String md5VideoString=md5VideoNode.getTextContent();
						model.setVideoMD5(md5VideoString);
						Log.i(TAG, "MD5 video ="+md5VideoString+" parsed!");
						
						
						
						
						Node idNode=attributesMap.getNamedItem("id");
						int modelId=Integer.parseInt(idNode.getTextContent());
						model.setId(modelId);
						Log.i(TAG, "Model ID="+modelId+" parsed!");
						
						Node codeNode=attributesMap.getNamedItem("code");
						model.setCode(codeNode.getTextContent());
						Log.i(TAG, "Model CODE="+codeNode.getTextContent()+" parsed!");
						
						Node titleNode=attributesMap.getNamedItem("title");
						String title=URLDecoder.decode(titleNode.getTextContent());
						model.setTitle(title);
						Log.i(TAG, "Model TITLE="+title+" parsed!");
						
						Node descNode=attributesMap.getNamedItem("desc");
						String description=URLDecoder.decode(descNode.getTextContent());
						model.setDesription(description);
						Log.i(TAG, "Model DESC="+description+" parsed!");
						
						Node shotsNode=attributesMap.getNamedItem("shots");
						int shots=Integer.parseInt(shotsNode.getTextContent());
						model.setShots(shots);
						Log.i(TAG, "Model SHOTS="+shots+" parsed!");
						
						Node timeNode=attributesMap.getNamedItem("time");
						int time=Integer.parseInt(timeNode.getTextContent());
						model.setTime(time);
						Log.i(TAG, "Model TIME="+time+" parsed!");
						
						Node priceNode=attributesMap.getNamedItem("price");
						float price=(float)Double.parseDouble(priceNode.getTextContent());
						model.setPrice(price);
						Log.i(TAG, "Model PRICE="+price+" parsed!");
						
						Node previewNode=attributesMap.getNamedItem("preview");
						String previewUrl=previewNode.getTextContent();
						Uri previewUri=Uri.parse(previewUrl);
						String previewFileName=previewUri.getLastPathSegment();
						model.setPreviewFile(previewFileName);
						Log.i(TAG, "Model PREVIEW="+previewFileName+" parsed!");
						
						Node pictureNode=attributesMap.getNamedItem("picture");
						String pictureUrl=pictureNode.getTextContent();
						Uri pictureUri=Uri.parse(pictureUrl);
						String pictureFileName=pictureUri.getLastPathSegment();
						model.setPictureFile(pictureFileName);
						Log.i(TAG, "Model PICTURE="+pictureFileName+" parsed!");
						
						Node videoNode=attributesMap.getNamedItem("ftp");
						String videoUrl=videoNode.getTextContent();
						Uri videoUri=Uri.parse(videoUrl);
						String videoFileName=videoUri.getLastPathSegment();
						model.setVideoFile(videoFileName);
						Log.i(TAG, "Model VIDEO="+videoFileName+" parsed!");
						
						modelsArray.add(model);
						
					}
				}				
			}
			
			onParserProgressEventsListener.onParsingSuccess(categoriesArray, modelsArray);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Parser Configuration Exception occurs!");
			onParserProgressEventsListener.onParsingError("Parser Configuration Exception occurs!");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "SAX Exception occurs!");
			onParserProgressEventsListener.onParsingError("SAX Exception occurs!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IO Exception occurs!");
			onParserProgressEventsListener.onParsingError("IO Exception occurs!");
		} catch (NumberFormatException e){
			e.printStackTrace();
			Log.e(TAG, "Number Format Exception occurs!");
			onParserProgressEventsListener.onParsingError("Number Format Exception occurs!");
		}

	}

}
