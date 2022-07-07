package com.owen.gui;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.owen.storage.StorageNode;

public class NodeTableModel implements TableModel {
	private List<StorageNode> nodeList;
	private int rowCount;
	public NodeTableModel(List<StorageNode> nodeList){
		this.nodeList = nodeList;
		this.rowCount = nodeList.size();
	}
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		return;
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "结点名称";
		case 1:
			return "结点IP";
		case 2:
			return "结点端口";
		case 3:
			return "总容量";
		case 4:
			return "剩余容量";
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		if(nodeList == null || nodeList.size() == 0){
			return null;
		}
		else{
			StorageNode node = nodeList.get(arg0);
			switch (arg1) {
			case 0:
				return node.getNodeName();
			case 1:
				return node.getNodeIP();
			case 2:
				return String.valueOf(node.getNodePort());
			case 3:
				return node.getVolume();
			case 4:
				return node.getLast();
			}
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		return;
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		return;
	}

}
