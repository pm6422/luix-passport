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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { useForm, useFieldArray } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { TeamService } from "@/services/team-service"
import { toast } from "sonner"
import { IconPlus, IconMinus } from "@tabler/icons-react"
import { useEffect } from "react"

const inviteSchema = z.object({
  invitations: z.array(z.object({
    email: z.string().trim().min(1, { message: "Required" }).email({ message: "Invalid email address" }),
    role: z.string().min(1, { message: "Role is required" })
  })).min(1)
})

interface InviteMembersDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  teamId: string
  onSuccess?: () => void
}

export function InviteMembersDialog({ open, onOpenChange, teamId, onSuccess }: InviteMembersDialogProps) {
  const form = useForm<z.infer<typeof inviteSchema>>({
    resolver: zodResolver(inviteSchema),
    defaultValues: {
      invitations: [{ email: "", role: "TEAM_MEMBER" }]
    }
  })

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "invitations"
  })

  useEffect(() => {
    if (open) {
      form.reset({ invitations: [{ email: "", role: "TEAM_MEMBER" }] })
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
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Invite Members</DialogTitle>
          <DialogDescription>
            Invite members to your team by entering their email and role.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-3 max-h-[60vh] overflow-y-auto p-1">
              {fields.map((field, index) => (
                <div
                  key={field.id}
                  className="grid grid-cols-[1fr_120px_40px] items-center gap-2"
                >
                  <div className="flex flex-col">
                    <Input
                      placeholder="Email address"
                      {...form.register(`invitations.${index}.email`)}
                    />
                    {form.formState.errors.invitations?.[index]?.email?.message && (
                      <span className="mt-1 text-xs text-destructive">
                        {form.formState.errors.invitations?.[index]?.email?.message as string}
                      </span>
                    )}
                  </div>

                  <div>
                    <Select
                      onValueChange={(val) =>
                        form.setValue(`invitations.${index}.role`, val, {
                          shouldValidate: true,
                          shouldDirty: true,
                        })
                      }
                      value={form.getValues(`invitations.${index}.role`) ?? "TEAM_MEMBER"}
                    >
                      <SelectTrigger className="h-10 w-full">
                        <SelectValue placeholder="Role" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="TEAM_OWNER">Owner</SelectItem>
                        <SelectItem value="TEAM_MEMBER">Member</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="flex justify-end">
                    {fields.length > 1 && (
                      <Button
                        type="button"
                        variant="outline"
                        size="icon"
                        onClick={() => remove(index)}
                        tabIndex={-1}
                        className="h-10 w-10"
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
              onClick={() => append({ email: "", role: "TEAM_MEMBER" })}
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
