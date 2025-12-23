import { ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { type User, userSchema, initialUserState } from "@/domains/user"
import { AccountService } from "@/services/account-service"
import { UserService } from "@/services/user-service"
import { RoleService } from "@/services/role-service"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type DataDict } from "@/domains/data-dict"
import type { DateTimeFormat } from "@/domains/date-time-format"
import { useLoginUserStore } from "@/stores/login-user-store"
import { UserFormFields } from "./user-form-fields"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: User) => Promise<void>
}

export function EditDialog({
  children,
  entityName,
  id,
  save
}: EditDialogProps) {
  const { loginUser } = useLoginUserStore()

  function afterSave(): void {
    if (loginUser.username === id) {
      AccountService.signOut();
    }
  }

  const loadEntity = async (entityId?: string | null) => {
    const [roleIdsRes, timezonesRes, dateTimeFormatsRes] = await Promise.all([
      RoleService.findAllIds(),
      AccountService.findSupportedTimezones(),
      AccountService.findDateTimeFormats()
    ])
    const tzOptions = timezonesRes.data.map((item: DataDict) =>
      ({ label: "(UTC" + item.dictName + ") " + item.dictCode, value: item.dictCode }))
    const dtfOptions = dateTimeFormatsRes.data.map((item: DateTimeFormat) =>
      ({ label: item.displayName + " (" + item.example + ")", value: item.id }))
    if (entityId) {
      const { data } = await UserService.findById(entityId)
      return { options: { enabledRoles: roleIdsRes.data, supportedTimezones: tzOptions, dateTimeFormats: dtfOptions }, entity: data }
    }
    return { options: { enabledRoles: roleIdsRes.data, supportedTimezones: tzOptions, dateTimeFormats: dtfOptions }, entity: initialUserState }
  }
  return (
    <FormDialog<User, { enabledRoles: string[], supportedTimezones: Option[], dateTimeFormats: Option[] }>
      entityName={entityName}
      id={id}
      schema={userSchema}
      defaultValues={initialUserState}
      save={save}
      afterSave={afterSave}
      onLoad={loadEntity}
      renderFields={(form, options) => (
        <UserFormFields
          form={form}
          id={id}
          loginUsername={loginUser.username}
          enabledRoles={options.enabledRoles}
          supportedTimezones={options.supportedTimezones}
          dateTimeFormats={options.dateTimeFormats}
        />
      )}
    >
      {children}
    </FormDialog>
  )
}
