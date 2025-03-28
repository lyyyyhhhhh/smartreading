package com.reading.ifaceutil.controller;

import ch.qos.logback.classic.Logger;
import com.reading.ifaceutil.model.Highlight;
import com.reading.ifaceutil.service.HighlightService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/highlight")
@CrossOrigin
public class HighlightController {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(HighlightController.class);

    @Autowired
    private HighlightService highlightService;

    @GetMapping("/all")
    public List<Highlight> getAllHighlights() {
        return highlightService.getAllHighlights();
    }

    @GetMapping("/user")
    public List<Highlight> getUserHighlights(@RequestParam Long userId, @RequestParam(required = false) Long articleId) {
        //logger.info("用户ID: {}, 文章ID: {}, 返回的高亮数据: {}", userId, articleId, highlights);
        return highlightService.getUserHighlights(userId, articleId);
    }

    @PostMapping
    public Highlight saveHighlight(@RequestBody Highlight highlight) {
        return highlightService.saveHighlight(highlight);
    }

    @DeleteMapping
    public void deleteHighlight(@RequestBody Highlight highlight) {
        highlightService.deleteHighlight(highlight);
    }

//    @DeleteMapping("/{id}")
//    public void deleteHighlight(@PathVariable Long id) {
//        highlightService.deleteHighlight(id);
//    }
}
