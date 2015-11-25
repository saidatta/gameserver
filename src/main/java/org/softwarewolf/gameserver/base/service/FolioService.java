package org.softwarewolf.gameserver.base.service;

import java.io.Serializable;
import java.util.List;

import org.softwarewolf.gameserver.base.domain.Folio;
import org.softwarewolf.gameserver.base.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.base.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolioService implements Serializable {
	@Autowired
	private FolioRepository folioRepository;

	private static final long serialVersionUID = 1L;

	public Folio save(Folio folio) {
		return folioRepository.save(folio);
	}
	
	public List<Folio> findAll() {
		return folioRepository.findAll();
	}
	
	public void initFolioCreator(FolioCreator folioCreator, Folio folio) {
		folioCreator.setPage(folio);
	}
}
