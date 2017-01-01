package com.esl.web.jsf.controller;

import com.esl.dao.IVocabImageDAO;
import com.esl.entity.VocabImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Controller
@Scope("session")
public class VocabImageController extends ESLController {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(VocabImageController.class);

	private static String imagesView = "/manage/vocabimage";
	private static int maxImage = 500;

	@Resource private IVocabImageDAO vocabImageDao;

	// ============== UI display data ================//
	private List<VocabImage> images;
	private int fromId;

	// ============== Setter / Getter ================//
	public List<VocabImage> getImages() { return images; }
	public void setImages(List<VocabImage> images) { this.images = images; }

	public int getFromId() { return fromId; }
	public void setFromId(int fromId) {	this.fromId = fromId; }

	// ============== Functions ================//
	public String listImage() {
		log.info("listImage: fromid {}, maxImage {}", fromId, maxImage);

		if (fromId < 1) fromId = Integer.MAX_VALUE;
		images = vocabImageDao.listLatest(maxImage, fromId);

		log.info("{} images retrieved", images.size());
		return imagesView;
	}
}
