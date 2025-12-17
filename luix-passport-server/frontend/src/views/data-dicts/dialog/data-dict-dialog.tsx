import { ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { Option } from "@/components/custom/form-field/multi-selector"
import { DataDictFormFields } from "./data-dict-form-fields"
import { type DataDict, dataDictSchema, initialDataDictState } from "@/domains/data-dict"
import { DataDictService } from "@/services/data-dict-service"

interface EditDialogProps {
  children: ReactNode
  entityName: string
  id?: string | null
  save: (formData: DataDict) => Promise<void>
}

export function EditDialog({
  children,
  entityName,
  id,
  save
}: EditDialogProps) {
  const loadEntity = async (entityId: string | null | undefined) => {
    const { data: allDicts } = await DataDictService.findAll(true)
    const uniqueCodes = Array.from(new Set(allDicts.content.map(item => item.categoryCode)))
    const categoryCodeOptions = uniqueCodes.map(code => ({ label: code, value: code }))
    if (entityId) {
      const { data } = await DataDictService.findById(entityId)
      return { options: { categoryCodeOptions }, entity: data }
    }
    return { options: { categoryCodeOptions }, entity: initialDataDictState }
  }

  return (
    <FormDialog<DataDict, { categoryCodeOptions: Option[] }>
      entityName={entityName}
      id={id}
      schema={dataDictSchema}
      defaultValues={initialDataDictState}
      save={save}
      onLoad={loadEntity}
      renderFields={(form, options) => (
        <DataDictFormFields form={form} categoryCodeOptions={options.categoryCodeOptions} />
      )}
    >
      {children}
    </FormDialog>
  )
}
