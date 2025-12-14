import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { z } from "zod"
import { TeamService } from "@/services/team-service"
import { useEffect, useState } from "react"
import { RoleService } from "@/services/role-service"
import { ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER } from "@/permission/roles"
import { InviteMembersFormFields } from "./invite-members-form-fields"

const inviteSchema = z.object({
  invitations: z.array(z.object({
    email: z.string().trim().min(1, { message: "Required" }).email({ message: "Invalid email address" }),
    roles: z.array(z.string()).min(1, { message: "Required" })
  })).min(1)
})

interface InviteMembersDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  teamId: string
  onSuccess?: () => void
}

export function InviteMembersDialog({ open, onOpenChange, teamId, onSuccess }: InviteMembersDialogProps) {
  const [allRoles, setAllRoles] = useState<string[]>([])

  useEffect(() => {
    if (open) {
      RoleService.findAllIds().then(({ data }) => setAllRoles(data))
    }
  }, [open])

  return (
    <FormDialog<z.infer<typeof inviteSchema>>
      entityName="invitation"
      schema={inviteSchema}
      defaultValues={{ invitations: [{ email: "", roles: [ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER] }] }}
      save={async (values) => {
        await TeamService.inviteMembers(teamId, values.invitations)
      }}
      afterSave={() => {
        onOpenChange(false)
        onSuccess?.()
      }}
      open={open}
      onOpenChange={onOpenChange}
      size="md"
      contentClassName="sm:max-w-[700px]"
      renderFields={(form) => (
        <InviteMembersFormFields form={form} allRoles={allRoles} />
      )}
    >
      <></>
    </FormDialog>
  )
}
