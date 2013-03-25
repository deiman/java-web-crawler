package com.jenkov.crawler.st.io;

import com.jenkov.crawler.util.UrlNormalizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 */
public class CrawlJob {

    protected Crawler        crawler       = null;
    protected String         urlToCrawl    = null;
    protected IPageProcessor pageProcessor = null;


    public CrawlJob(String urlToCrawl, IPageProcessor pageProcessor, Crawler crawler) {
        this.urlToCrawl    = urlToCrawl;
        this.pageProcessor = pageProcessor;
        this.crawler       = crawler;
    }
    
    public void crawl() throws IOException{

        URL url = new URL(this.urlToCrawl);

        URLConnection urlConnection = null;
        try {
            urlConnection = url.openConnection();

            try (InputStream input = urlConnection.getInputStream()) {

                Document doc      = Jsoup.parse(input, "UTF-8", "");
                Elements elements = doc.select("a");

                String baseUrl = url.toExternalForm();
                for(Element element : elements){
                    String linkUrl       = element.attr("href");
                    String normalizedUrl = UrlNormalizer.normalize(linkUrl, baseUrl);

                    this.crawler.addUrl(normalizedUrl);
                }
                if(this.pageProcessor != null) {
                    this.pageProcessor.process(doc);
                }

            } catch (IOException e) {
                throw new RuntimeException("Error connecting to URL", e);
            }
        } catch(IOException e) {
            throw new RuntimeException("Error connecting to URL", e);
        }
    }

}
