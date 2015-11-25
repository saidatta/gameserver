package org.softwarewolf.gameserver.base.service;

import java.io.Serializable;
import java.util.List;

import org.softwarewolf.gameserver.base.domain.Page;
import org.softwarewolf.gameserver.base.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageService implements Serializable {
	@Autowired
	private PageRepository pageRepository;

	private static final long serialVersionUID = 1L;

	public Page save(Page page) {
		return pageRepository.save(page);
	}
	
	public List<Page> findAll() {
		return pageRepository.findAll();
	}
}
