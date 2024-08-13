package todoapp.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import todoapp.web.model.SiteProperties;

import java.util.Objects;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final SiteProperties siteProperties;

    public GlobalControllerAdvice(SiteProperties siteProperties) {
        this.siteProperties = Objects.requireNonNull(siteProperties);
    }

    @ModelAttribute("site")
    public SiteProperties siteProperties() {
        return siteProperties;
    }

}
