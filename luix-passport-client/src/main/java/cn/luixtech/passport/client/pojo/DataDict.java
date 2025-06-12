package cn.luixtech.passport.client.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDict implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String num;

    private String categoryCode;

    private String dictCode;

    private String dictName;

    private String remark;

    private Boolean enabled;
}
