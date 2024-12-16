package com.company.customeremulation.view.params;

import com.company.customeremulation.entity.Params;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "paramses/:id", layout = MainView.class)
@ViewController(id = "cst_Params.detail")
@ViewDescriptor(path = "params-detail-view.xml")
@EditedEntityContainer("paramsDc")
public class ParamsDetailView extends StandardDetailView<Params> {
}