import { ReactNode } from 'react'
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { type Permission, permissionSchema, initialPermissionState } from "@/domains/permission"
import { PermissionService } from "@/services/permission-service"
import { DataDictService } from "@/services/data-dict-service"
import { Option } from "@/components/custom/form-field/multi-selector"
import type { DataDict } from "@/domains/data-dict"
import { PermissionFormFields } from "./permission-form-fields"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Permission) => Promise<void>
}

export function EditDialog({
  children,
  entityName,
  id,
  save
}: EditDialogProps) {
  const loadEntity = async (entityId?: string | null) => {
    const [resourceTypesRes, actionsRes] = await Promise.all([
      DataDictService.lookup("PermissionResourceType", true),
      DataDictService.lookup("PermissionAction", true),
    ])
    const resourceTypeOptions = resourceTypesRes.data.content.map((item: DataDict) => ({ label: item.dictCode, value: item.dictCode }))
    const actionOptions = actionsRes.data.content.map((item: DataDict) => ({ label: item.dictCode, value: item.dictCode }))
    if (entityId) {
      const { data } = await PermissionService.findById(entityId)
      return { options: { resourceTypes: resourceTypeOptions, actions: actionOptions }, entity: data }
    }
    return { options: { resourceTypes: resourceTypeOptions, actions: actionOptions }, entity: initialPermissionState }
  }

  return (
    <FormDialog<Permission, { resourceTypes: Option[], actions: Option[] }>
      entityName={entityName}
      id={id}
      schema={permissionSchema}
      defaultValues={initialPermissionState}
      save={save}
      onLoad={loadEntity}
      renderFields={(form, options) => (
        <PermissionFormFields form={form} resourceTypes={options.resourceTypes} actions={options.actions} />
      )}
    >
      {children}
    </FormDialog>
  )
}
