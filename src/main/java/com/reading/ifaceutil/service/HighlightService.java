package com.reading.ifaceutil.service;

import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.model.Highlight;
import com.reading.ifaceutil.repository.highlight.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighlightService {

    @Autowired
    private HighlightRepository highlightRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

    public List<Highlight> getAllHighlights() {
        return highlightRepository.findAll();
    }

    public List<Highlight> getUserHighlights(Long userId, Long articleId) {
         return highlightRepository.selectUserHighlights(userId, articleId);
    }

    public Highlight saveHighlight(Highlight highlight) {
        kafkaProducer.sendCommentEvent(highlight.getArticleId(), highlight.getUserId(), highlight.getComment());
        return highlightRepository.save(highlight);
    }

    public void deleteHighlight(Highlight highlight) {
        highlightRepository.deleteById(highlight.getId());
    }
}
