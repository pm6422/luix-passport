package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class BatchUpdateDataDict implements Serializable {
    private List<String> ids;
    private String       targetCategoryCode;
}