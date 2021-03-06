package com.adobe.dx.admin.config.manager;

import static com.adobe.dx.admin.config.manager.Constants.BAD_LIST;
import static com.adobe.dx.admin.config.manager.Constants.FOLDER_TYPES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.day.cq.wcm.api.Page;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = { Resource.class },
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ColumnViewItem {

    @Self
    private Resource resource;

    @ValueMapValue(name = "jcr:primaryType")
    private String primaryType;

    @ValueMapValue(name = "jcr:title")
    private String title;

    private Page page;

    @PostConstruct
    private void init() {
        page = resource.adaptTo(Page.class);
    }

    public boolean getIsPage() {
        return page != null;
    }

    public String getLabel() {
        if (page != null && page.getTitle() != null) {
            return page.getTitle();
        }
        if (title != null) {
            return title;
        }
        return resource.getName();
    }

    public String getName() {
        return resource.getName();
    }

    public String getPath() {
        return resource.getPath();
    }

    public String getIconType() {
        boolean isFolder = Arrays.stream(FOLDER_TYPES).anyMatch(primaryType::equals);
        if (isFolder) {
            return "folder";
        }
        return "config";
    }

    public List<ColumnViewItem> getChildren() {
        List<ColumnViewItem> children = new ArrayList<>();
        Iterable<Resource> childrenIter = resource.getChildren();
        childrenIter.forEach(child -> {
            boolean badListed = Arrays.stream(BAD_LIST).anyMatch(child.getName()::equals);
            if (!badListed) {
                ColumnViewItem item = child.adaptTo(ColumnViewItem.class);
                children.add(item);
            }
        });
        return children;
    }
}