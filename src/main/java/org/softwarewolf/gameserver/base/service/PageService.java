package org.softwarewolf.gameserver.base.service;

import java.io.Serializable;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.stereotype.Service;

@Service
public class PageService implements Serializable {

	private static final long serialVersionUID = 1L;

	public ObjectTag createTag(Object obj, String campaignId) {
		ObjectTag tag = new ObjectTag();
		tag.setCampaignId(campaignId);
		tag.setClassName(obj.getClass().getName());
		tag.setObjectId(objectId);
		return tag;
	}
}
