import { useState, ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { type User, userSchema, initialUserState } from "@/domains/user"
import { AccountService } from "@/services/account-service"
import { UserService } from "@/services/user-service"
import { RoleService } from "@/services/role-service"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type DataDict } from "@/domains/data-dict"
import type { SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
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
  const [enabledRoles, setEnabledRoles] = useState<string[]>([])
  const [supportedTimezones, setSupportedTimezones] = useState(Array<Option>)
  const [supportedDateTimeFormats, setSupportedDateTimeFormats] = useState(Array<Option>)

  function afterSave(): void {
    if (loginUser.username === id) {
      AccountService.signOut();
    }
  }

  return (
    <FormDialog<User, { enabledRoles: string[], supportedTimezones: Option[], supportedDateTimeFormats: Option[] }>
      entityName={entityName}
      id={id}
      schema={userSchema}
      defaultValues={initialUserState}
      save={save}
      afterSave={afterSave}
      onLoad={async (entityId) => {
        const [roleIdsRes, timezonesRes, dateTimeFormatsRes] = await Promise.all([
          RoleService.findAllIds(),
          AccountService.findSupportedTimezones(),
          AccountService.findSupportedDateTimeFormats()
        ])
        setEnabledRoles(roleIdsRes.data)
        const tzOptions = timezonesRes.data.map((item: DataDict) =>
          ({ label: "(UTC" + item.dictName + ") " + item.dictCode, value: item.dictCode }))
        setSupportedTimezones(tzOptions)
        const dtfOptions = dateTimeFormatsRes.data.map((item: SupportedDateTimeFormat) =>
          ({ label: item.displayName + " (" + item.example + ")", value: item.id }))
        setSupportedDateTimeFormats(dtfOptions)
        if (entityId) {
          const { data } = await UserService.findById(entityId)
          return { options: { enabledRoles: roleIdsRes.data, supportedTimezones: tzOptions, supportedDateTimeFormats: dtfOptions }, entity: data }
        }
        return { options: { enabledRoles: roleIdsRes.data, supportedTimezones: tzOptions, supportedDateTimeFormats: dtfOptions }, entity: initialUserState }
      }}
      renderFields={(form) => (
        <UserFormFields
          form={form}
          id={id}
          loginUsername={loginUser.username}
          enabledRoles={enabledRoles}
          supportedTimezones={supportedTimezones}
          supportedDateTimeFormats={supportedDateTimeFormats}
        />
      )}
    >
      {children}
    </FormDialog>
  )
}

