import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Form } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { useForm, useFieldArray } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { TeamService } from "@/services/team-service"
import { toast } from "sonner"
import { IconPlus, IconMinus } from "@tabler/icons-react"
import { useEffect, useState } from "react"
import { RoleService } from "@/services/role-service"
import { TeamRoleSelector } from "@/components/custom/form-field/team-role-selector"
import { ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER } from "@/constants/role-constants"

const inviteSchema = z.object({
  invitations: z.array(z.object({
    email: z.string().trim().min(1, { message: "Required" }).email({ message: "Invalid email address" }),
    roles: z.array(z.string()).min(1, { message: "Role is required" })
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

  const form = useForm<z.infer<typeof inviteSchema>>({
    resolver: zodResolver(inviteSchema),
    defaultValues: {
      invitations: [{ email: "", roles: [ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER] }]
    }
  })

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "invitations"
  })

  useEffect(() => {
    if (open) {
      form.reset({ invitations: [{ email: "", roles: [ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER] }] })
    }
  }, [open, form])

  async function onSubmit(values: z.infer<typeof inviteSchema>) {
    try {
      await TeamService.inviteMembers(teamId, values.invitations)
      toast.success("Invitation sent", {
        description: "Members have been invited successfully."
      })
      onOpenChange(false)
      onSuccess?.()
    } catch (error) {
      toast.error("Error", {
        description: "Failed to send invitations."
      })
      console.error(error)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[700px]">
        <DialogHeader>
          <DialogTitle>Invite Members</DialogTitle>
          <DialogDescription>
            Invite members to your team by entering their email and roles.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-3 max-h-[60vh] overflow-y-auto p-1">
              {fields.map((field, index) => (
                <div
                  key={field.id}
                  className="grid grid-cols-[1fr_250px_40px] items-start gap-2"
                >
                  <div className="flex flex-col">
                    <Input
                      placeholder="Email address"
                      {...form.register(`invitations.${index}.email`)}
                      className="h-9"
                    />
                    {form.formState.errors.invitations?.[index]?.email?.message && (
                      <span className="mt-1 text-xs text-destructive">
                        {form.formState.errors.invitations?.[index]?.email?.message as string}
                      </span>
                    )}
                  </div>

                  <div className="flex flex-col">
                    <TeamRoleSelector
                      value={form.watch(`invitations.${index}.roles`) || []}
                      allRoles={allRoles}
                      onChange={(newRoles) => {
                        form.setValue(`invitations.${index}.roles`, newRoles, {
                          shouldValidate: true,
                          shouldDirty: true,
                        })
                      }}
                      className="w-full h-9"
                    />
                    {form.formState.errors.invitations?.[index]?.roles?.message && (
                      <span className="mt-1 text-xs text-destructive">
                        {form.formState.errors.invitations?.[index]?.roles?.message as string}
                      </span>
                    )}
                  </div>

                  <div className="flex justify-end pt-0">
                    {fields.length > 1 && (
                      <Button
                        type="button"
                        variant="outline"
                        size="icon"
                        onClick={() => remove(index)}
                        tabIndex={-1}
                        className="h-9 w-9"
                      >
                        <IconMinus className="size-4" />
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>

            <Button
              type="button"
              variant="outline"
              size="sm"
              className="mt-1 ml-1"
              onClick={() => append({ email: "", roles: [ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER] })}
            >
              <IconPlus className="mr-2 size-4" />
              Add another
            </Button>

            <DialogFooter>
              <Button type="submit">Send Invitations</Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
