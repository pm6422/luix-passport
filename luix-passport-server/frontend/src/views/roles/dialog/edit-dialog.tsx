import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectFormField from "@/components/custom/form-field/multi-select"
import { Option } from "@/components/custom/multi-select"
import { type Role, roleSchema, initialRoleState } from "@/domains/role"
import { RoleService } from "@/services/role-service"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Role) => Promise<void>,
  afterSave?: (success: boolean) => void
}

export function EditDialog({
  children,
  entityName,
  id,
  save,
  afterSave
}: EditDialogProps) {
  const [open, setOpen] = useState(false)
  const [grantTypeOptions, setGrantTypeOptions] = useState(Array<Option>)
  const form = useForm<Role>({
    resolver: zodResolver(roleSchema),
    defaultValues: initialRoleState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    id && RoleService.findById(id).then(r => {
      form.reset(r.data)
    })
  }, [form, id, open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen} debug={false}>
        <InputFormField
          control={form.control} 
          name="id"
          label="ID"
          required
        />

        {/*<MultiSelectFormField*/}
        {/*  control={form.control} */}
        {/*  name="clientAuthenticationMethods"*/}
        {/*  label="Authentication Methods"*/}
        {/*  required*/}
        {/*  options={authenticationMethodOptions}*/}
        {/*  multiple={true}*/}
        {/*/>*/}

      </SaveDialogContent>
    </Dialog>
  )
}
