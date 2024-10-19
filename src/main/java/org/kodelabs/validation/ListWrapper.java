package org.kodelabs.validation;

import javax.validation.Valid;
import java.util.List;

public class ListWrapper<T> {
    @Valid
    List<T> list;

    public ListWrapper() {}

    public ListWrapper(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
