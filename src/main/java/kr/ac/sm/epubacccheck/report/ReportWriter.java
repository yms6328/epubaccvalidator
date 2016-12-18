package kr.ac.sm.epubacccheck.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.ac.sm.epubacccheck.message.CustomMessageHandler;
import kr.ac.sm.epubacccheck.message.Message;
import kr.ac.sm.epubacccheck.message.MessageId;
import kr.ac.sm.epubacccheck.message.MessageLocationMap;

public class ReportWriter
{
	private List<Message> messages;
	private HashMap<String, List<Message>> messageMap;

	public ReportWriter()
	{
		messages = new ArrayList<Message>();
		messageMap = new LinkedHashMap<String, List<Message>>();
	}
	
	public void writeReport(String filePath)
	{
		File reportFile = new File(filePath);
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<MessageId> messageIdList = new ArrayList<MessageId>();
		ArrayList<MessageId> customMessageIds = new ArrayList<MessageId>();
		ArrayList<String> customMessages = new ArrayList<String>();
		MessageId customMessageId;
		
		customMessageIds = CustomMessageHandler.getCustomMessageIdList();
		
		for (MessageId messageId : MessageId.values())
		{
			List<EPUBLocation> locations = MessageLocationMap.getEPUBLocationList(messageId);
			if (locations.size() != 0)
			{
				messageIdList.add(messageId);
			}
		}

		if (!customMessageIds.isEmpty() || customMessageIds.size() != 0)
		{
			for (int messageIdIndex = 0; messageIdIndex < messageIdList.size(); messageIdIndex++)
			{
				customMessageId = customMessageIds.get(messageIdIndex);
				if (customMessageId.equals(messageIdList.get(messageIdIndex)))
				{
					customMessages = CustomMessageHandler.getCustomMessages(customMessageIds.get(messageIdIndex));
					for (int customMessageIndex = 0; customMessageIndex < customMessages.size(); customMessageIndex++)
					{
						messages.add(new Message(customMessageId, customMessages.get(customMessageIndex)));
					}
				}
				else
				{
					messages.add(new Message(messageIdList.get(messageIdIndex)));
				}
			}
		}
		else
		{
			for (int messageIdIndex = 0; messageIdIndex < messageIdList.size(); messageIdIndex++)
			{
				messages.add(new Message(messageIdList.get(messageIdIndex)));
			}
		}

		messageMap.put("message", messages);
		
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(reportFile, messageMap);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
