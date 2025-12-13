import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
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
import { IconPlus, IconTrash } from "@tabler/icons-react"
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
      invitations: [{ email: "", role: "MEMBER" }]
    }
  })

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: "invitations"
  })

  useEffect(() => {
    if (open) {
      form.reset({ invitations: [{ email: "", role: "MEMBER" }] })
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
      <DialogContent className="sm:max-w-[550px]">
        <DialogHeader>
          <DialogTitle>Invite Members</DialogTitle>
          <DialogDescription>
            Invite members to your team by entering their email and role.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-4 max-h-[60vh] overflow-y-auto p-1">
              {fields.map((field, index) => (
                <div key={field.id} className="flex gap-1 items-start">
                  <FormField
                    control={form.control}
                    name={`invitations.${index}.email`}
                    render={({ field }) => (
                      <FormItem className="flex-1">
                        <FormControl>
                          <Input placeholder="Email address" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name={`invitations.${index}.role`}
                    render={({ field }) => (
                      <FormItem className="w-[120px]">
                        <Select onValueChange={field.onChange} value={field.value ?? "MEMBER"}>
                          <FormControl>
                            <SelectTrigger className="h-10 w-full">
                              <SelectValue placeholder="Role" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="OWNER">Owner</SelectItem>
                            <SelectItem value="MEMBER">Member</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  {fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      onClick={() => remove(index)}
                      className="mt-[2px]"
                      tabIndex={-1}
                    >
                      <IconTrash className="size-4 text-destructive" />
                    </Button>
                  )}
                </div>
              ))}
            </div>

            <Button
              type="button"
              variant="outline"
              size="sm"
              className="mt-2"
              onClick={() => append({ email: "", role: "MEMBER" })}
            >
              <IconPlus className="mr-2 size-4" />
              Add another
            </Button>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                Cancel
              </Button>
              <Button type="submit">Send Invitations</Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
