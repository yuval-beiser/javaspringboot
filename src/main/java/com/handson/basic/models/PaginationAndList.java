package com.handson.basic.models;


import java.util.List;
import java.util.Objects;

public class PaginationAndList {
    private Pagination pagination;
    private List data;

    public PaginationAndList() {
    }

    public static PaginationAndList of(Pagination pagination, List data) {
        PaginationAndList res = new PaginationAndList();
        res.pagination = pagination;
        res.data = data;
        return res;
    }

    public Pagination getPagination() {
        return this.pagination;
    }

    public List getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PaginationAndList that = (PaginationAndList) o;
            return Objects.equals(this.pagination, that.pagination) && Objects.equals(this.data, that.data);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.pagination, this.data);
    }

    public String toString() {
        return "PaginationAndList{pagination=" + this.pagination + ", data=" + this.data + "}";
    }
}
