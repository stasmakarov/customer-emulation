package com.company.customeremulation.view.params;

import com.company.customeremulation.entity.Params;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "paramses", layout = MainView.class)
@ViewController(id = "cst_Params.list")
@ViewDescriptor(path = "params-list-view.xml")
@LookupComponent("paramsesDataGrid")
@DialogMode(width = "64em")
public class ParamsListView extends StandardListView<Params> {
}