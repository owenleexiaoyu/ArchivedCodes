package com.owen.gui;

import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.owen.storage.SFile;

public class FileTableModel implements TableModel{
	private List<SFile> fileList;
	private int rowCount = 0;
	
	public FileTableModel(List<SFile> fileList) {
		this.fileList = fileList;
		this.rowCount = fileList.size();
	}
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: // 全为字符串类型
		case 1:
		case 2:
		case 3:
			return String.class;
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "文件uuid";
		case 1:
			return "文件大小";
		case 2:
			return "主节点";
		case 3:
			return "备份结点";
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SFile f = fileList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return f.getUuid();
		case 1:
			return String.valueOf(f.getSize());
		case 2:
			return f.getStorageNode1().getNodeName();
		case 3:
			return f.getStorageNode2().getNodeName();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//不可编辑
		if(columnIndex == 0){
			return true;
		}
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		return;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}
	
}
