package cn.lsu.community.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntity<T extends BaseEntity<?>> extends Model<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2049409488631452486L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "create_date",fill = FieldFill.INSERT)
	private Date createDate;
	
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "last_modified",fill = FieldFill.UPDATE)
	private Date lastModified;
	
	@Override
	protected Serializable pkVal() {
		return this.id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.getId() != null ? this.getId().hashCode() : 0);

		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (!(object instanceof BaseEntity)) {
			return false;
		}
		if (!getClass().isAssignableFrom(object.getClass()) && !object.getClass().isAssignableFrom(getClass())) {
			return false;
		}
		BaseEntity<?> other = (BaseEntity<?>) object;
		if (other.getId() == null || getId() == null) {
			return false;
		}
		if (other.getId().equals(getId())) {
			return true;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " [ID=" + id + "]";
	}
}
