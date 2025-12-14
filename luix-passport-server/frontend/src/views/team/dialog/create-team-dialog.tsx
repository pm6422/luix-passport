import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { z } from "zod"
import { TeamService } from "@/services/team-service"
import { AvatarUploader } from "@/components/custom/uploader/avatar-uploader"
import type { ReactNode } from "react"

const createTeamSchema = z.object({
  name: z.string().trim().min(1, { message: "Required" }),
  photo: z.any().optional()
})

type CreateTeam = z.infer<typeof createTeamSchema>

interface CreateTeamDialogProps {
  children?: ReactNode
  onSuccess?: () => void
  open?: boolean
  onOpenChange?: (open: boolean) => void
}

export function CreateTeamDialog({ children, onSuccess, open, onOpenChange }: CreateTeamDialogProps) {
  async function save(model: CreateTeam): Promise<void> {
    await TeamService.create(model.name, model.photo)
    onSuccess?.()
  }

  return (
    <FormDialog<CreateTeam>
      entityName="team"
      schema={createTeamSchema}
      defaultValues={{ name: "", photo: null }}
      contentClassName="max-w-[450px] sm:max-w-[450px] lg:max-w-[450px]"
      save={save}
      afterSave={() => onSuccess?.()}
      open={open}
      onOpenChange={onOpenChange}
      renderFields={(form) => (
        <>
          <div className="flex justify-center">
            <FormField
              control={form.control}
              name={"photo"}
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <AvatarUploader
                      onValueChange={field.onChange}
                      className="size-32"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
          <FormField
            control={form.control}
            name={"name"}
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input placeholder="Team name" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </>
      )}
    >
      {children}
    </FormDialog>
  )
}
