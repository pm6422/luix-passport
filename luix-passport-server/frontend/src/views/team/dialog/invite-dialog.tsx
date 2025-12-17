import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { z } from "zod"
import { TeamService } from "@/services/team-service"
import { useEffect, useState } from "react"
import { ROLE_ANONYMOUS, ROLE_USER, ROLE_TEAM_MEMBER } from "@/domains/role"
import { InviteMembersFormFields } from "./invite-members-form-fields"

const invitationSchema = z.object({
  inviteeEmail: z.string().trim().min(1, { message: "Required" }).email({ message: "Invalid email address" }),
  inviteeRoles: z.array(z.string()).min(1, { message: "Required" })
})

const baseInviteSchema = z.object({
  invitations: z
    .array(invitationSchema)
    .min(1)
    .superRefine((invitations, ctx) => {
      const seen = new Map<string, number[]>()
      invitations.forEach((inv, index) => {
        const email = (inv.inviteeEmail || "").trim().toLowerCase()
        if (!email) return
        const indices = seen.get(email) || []
        indices.push(index)
        seen.set(email, indices)
      })
      seen.forEach((indices) => {
        if (indices.length > 1) {
          indices.forEach((i) => {
            ctx.addIssue({
              code: z.ZodIssueCode.custom,
              path: [i, "inviteeEmail"],
              message: "Duplicate email"
            })
          })
        }
      })
    })
})

interface InviteMembersDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  teamId: string
  onSuccess?: () => void
}

export function InviteMembersDialog({ open, onOpenChange, teamId, onSuccess }: InviteMembersDialogProps) {
  const [allRoles, setAllRoles] = useState<string[]>([])
  const [blockedEmails, setBlockedEmails] = useState<Set<string>>(new Set())

  useEffect(() => {
    if (open) {
      TeamService.findAllRoleIds().then(({ data }) => setAllRoles(data))
      TeamService.getPendingInvitations(teamId).then(({ data }) => {
        const s = new Set<string>()
        data.forEach((i) => {
          const status = (i.status || "").toUpperCase()
          if (status !== "EXPIRED" && status !== "ACCEPTED" && status !== "REJECTED") {
            // Check inviteeEmail if available, otherwise fallback (though backend should return consistent fields)
            const email = (i.inviteeEmail || "").trim().toLowerCase()
            if (email) s.add(email)
          }
        })
        setBlockedEmails(s)
      }).catch(() => {
        setBlockedEmails(new Set())
      })
    }
  }, [open, teamId])

  const inviteSchema = baseInviteSchema.superRefine((values, ctx) => {
    values.invitations.forEach((inv, index) => {
      const email = (inv.inviteeEmail || "").trim().toLowerCase()
      if (email && blockedEmails.has(email)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ["invitations", index, "inviteeEmail"],
          message: "Email already invited and pending"
        })
      }
    })
  })

  return (
    <FormDialog<z.infer<typeof inviteSchema>>
      entityName="invitation"
      loadingMessage="Sending invitation mail..."
      successMessage="Invitation mail sent successfully"
      schema={inviteSchema}
      defaultValues={{ invitations: [{ inviteeEmail: "", inviteeRoles: [ROLE_ANONYMOUS, ROLE_USER, ROLE_TEAM_MEMBER] }] }}
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
      showFormErrors={false}
      savingButtonText="Sending..."
      saveButtonText="Send Invitation"
      renderFields={(form) => (
        <InviteMembersFormFields form={form} allRoles={allRoles} />
      )}
    >
      <></>
    </FormDialog>
  )
}
