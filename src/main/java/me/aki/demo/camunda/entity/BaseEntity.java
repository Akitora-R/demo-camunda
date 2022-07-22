package me.aki.demo.camunda.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class BaseEntity {
    /**
     * 创建人
     */
    @ApiModelProperty(name = "创建人")
    private String crtId;
    /**
     * 创建机构
     */
    @ApiModelProperty(name = "创建机构")
    private String crtOrgCode;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime crtTm;
    /**
     * 修改人
     */
    @ApiModelProperty(name = "修改人")
    private String mdfId;
    /**
     * 修改机构
     */
    @ApiModelProperty(name = "修改机构")
    private String mdfOrgCode;
    /**
     * 修改时间
     */
    @ApiModelProperty(name = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime mdfTm;
    /**
     * 租户ID
     */
    @ApiModelProperty(name = "租户ID")
    private String tenantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(crtId, that.crtId) && Objects.equals(crtOrgCode, that.crtOrgCode) && Objects.equals(crtTm, that.crtTm) && Objects.equals(mdfId, that.mdfId) && Objects.equals(mdfOrgCode, that.mdfOrgCode) && Objects.equals(mdfTm, that.mdfTm) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crtId, crtOrgCode, crtTm, mdfId, mdfOrgCode, mdfTm, tenantId);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "crtId='" + crtId + '\'' +
                ", crtOrgCode='" + crtOrgCode + '\'' +
                ", crtTm=" + crtTm +
                ", mdfId='" + mdfId + '\'' +
                ", mdfOrgCode='" + mdfOrgCode + '\'' +
                ", mdfTm=" + mdfTm +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
