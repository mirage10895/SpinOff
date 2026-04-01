package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiObjectResponse<T> {
    private List<T> results;
}
