import { ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { GeneratePromoCode, generatePromoCodeSchema, initialGeneratePromoCodeState } from "@/domains/promo-code"
import { RoleService } from "@/services/role-service"
import { GeneratePromoCodeFormFields } from "./generate-form-fields"
import { Option } from "@/components/custom/form-field/multi-selector"

interface GenerateDialogProps {
  children: ReactNode
  entityName: string
  save: (formData: GeneratePromoCode) => Promise<void>
}

export function GenerateDialog({
  children,
  entityName,
  save
}: GenerateDialogProps) {

  const loadEntity = async () => {
    const roleIdsRes = await RoleService.findAllIds()
    const roleOptions = roleIdsRes.data.map((roleId: string) => ({
      label: roleId,
      value: roleId
    }))
    return { options: { roleOptions }, entity: initialGeneratePromoCodeState }
  }

  return (
    <FormDialog<GeneratePromoCode, { roleOptions: Option[] }>
      entityName={entityName}
      schema={generatePromoCodeSchema}
      defaultValues={initialGeneratePromoCodeState}
      save={save}
      onLoad={loadEntity}
      renderFields={(form, options) => (
        <GeneratePromoCodeFormFields
          form={form}
          roleOptions={options.roleOptions}
        />
      )}
    >
      {children}
    </FormDialog>
  )
}
