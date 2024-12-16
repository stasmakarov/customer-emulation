package com.company.customeremulation.view.itempersisted;

import com.company.customeremulation.entity.ItemPersisted;
import com.company.customeremulation.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;

@Route(value = "itemPersisteds", layout = MainView.class)
@ViewController(id = "cst_ItemPersisted.list")
@ViewDescriptor(path = "item-persisted-list-view.xml")
@LookupComponent("itemPersistedsDataGrid")
@DialogMode(width = "64em")
public class ItemPersistedListView extends StandardListView<ItemPersisted> {

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<ItemPersisted> itemPersistedsDc;

    @ViewComponent
    private InstanceContainer<ItemPersisted> itemPersistedDc;

    @ViewComponent
    private InstanceLoader<ItemPersisted> itemPersistedDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private HorizontalLayout detailActions;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        updateControls(false);
    }

    @Subscribe("itemPersistedsDataGrid.create")
    public void onItemPersistedsDataGridCreate(final ActionPerformedEvent event) {
        dataContext.clear();
        ItemPersisted entity = dataContext.create(ItemPersisted.class);
        itemPersistedDc.setItem(entity);
        updateControls(true);
    }

    @Subscribe("itemPersistedsDataGrid.edit")
    public void onItemPersistedsDataGridEdit(final ActionPerformedEvent event) {
        updateControls(true);
    }

    @Subscribe("saveButton")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        ItemPersisted item = itemPersistedDc.getItem();
        ValidationErrors validationErrors = validateView(item);
        if (!validationErrors.isEmpty()) {
            ViewValidation viewValidation = getViewValidation();
            viewValidation.showValidationErrors(validationErrors);
            viewValidation.focusProblemComponent(validationErrors);
            return;
        }
        dataContext.save();
        itemPersistedsDc.replaceItem(item);
        updateControls(false);
    }

    @Subscribe("cancelButton")
    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
        dataContext.clear();
        itemPersistedDl.load();
        updateControls(false);
    }

    @Subscribe(id = "itemPersistedsDc", target = Target.DATA_CONTAINER)
    public void onItemPersistedsDcItemChange(final InstanceContainer.ItemChangeEvent<ItemPersisted> event) {
        ItemPersisted entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            itemPersistedDl.setEntityId(entity.getId());
            itemPersistedDl.load();
        } else {
            itemPersistedDl.setEntityId(null);
            itemPersistedDc.setItem(null);
        }
        updateControls(false);
    }

    protected ValidationErrors validateView(ItemPersisted entity) {
        ViewValidation viewValidation = getViewValidation();
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
    }

    private void updateControls(boolean editing) {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(!editing);
            }
        });

        detailActions.setVisible(editing);
        listLayout.setEnabled(!editing);
    }

    private ViewValidation getViewValidation() {
        return getApplicationContext().getBean(ViewValidation.class);
    }
}