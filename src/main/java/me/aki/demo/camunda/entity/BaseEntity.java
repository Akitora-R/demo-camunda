package me.aki.demo.camunda.entity;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private LocalDateTime mdfTm;
    /**
     * 租户ID
     */
    @ApiModelProperty(name = "租户ID")
    private String tenantId;

    public String getCrtId() {
        return crtId;
    }

    public void setCrtId(String crtId) {
        this.crtId = crtId;
    }

    public String getCrtOrgCode() {
        return crtOrgCode;
    }

    public void setCrtOrgCode(String crtOrgCode) {
        this.crtOrgCode = crtOrgCode;
    }

    public LocalDateTime getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(LocalDateTime crtTm) {
        this.crtTm = crtTm;
    }

    public String getMdfId() {
        return mdfId;
    }

    public void setMdfId(String mdfId) {
        this.mdfId = mdfId;
    }

    public String getMdfOrgCode() {
        return mdfOrgCode;
    }

    public void setMdfOrgCode(String mdfOrgCode) {
        this.mdfOrgCode = mdfOrgCode;
    }

    public LocalDateTime getMdfTm() {
        return mdfTm;
    }

    public void setMdfTm(LocalDateTime mdfTm) {
        this.mdfTm = mdfTm;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

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
