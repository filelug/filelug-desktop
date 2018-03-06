package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>ResponseListRootsModel</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResponseListRootsModel extends ResponseModel {

    @JsonProperty("roots")
    private List<RootDirectory> roots;

    public ResponseListRootsModel() {
        super();
    }

    public ResponseListRootsModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, List<RootDirectory> roots) {
        super(sid, status, error, operatorId, timestamp);
        this.roots = roots;
    }

    public List<RootDirectory> getRoots() {
        return roots;
    }

    public void setRoots(List<RootDirectory> roots) {
        this.roots = roots;
    }

    public void addRoot(RootDirectory root) {
        if (roots == null) {
            roots = new ArrayList<>();
        }

        roots.add(root);
    }

    public void removeRoot(RootDirectory root) {
        if (roots != null && roots.size() > 0) {
            roots.remove(root);
        }
    }
}
