package sn.sdley.springbootstarter0.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Page page = new Page();

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public static class Page {
        private int size = 10;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
