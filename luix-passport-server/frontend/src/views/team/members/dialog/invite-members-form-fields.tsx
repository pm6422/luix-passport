import { UseFormReturn } from "react-hook-form"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { TeamRoleSelector } from "@/components/custom/form-field/team-role-selector"
import { IconPlus, IconMinus } from "@tabler/icons-react"
import { ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER } from "@/permission/roles"

type Invitation = { email: string; roles: string[] }
type InviteForm = { invitations: Invitation[] }

interface InviteMembersFormFieldsProps {
  form: UseFormReturn<InviteForm>
  allRoles: string[]
}

export function InviteMembersFormFields({
  form,
  allRoles
}: InviteMembersFormFieldsProps) {
  const invitations = form.watch("invitations") || []

  const append = () => {
    form.setValue("invitations", [...invitations, { email: "", roles: [ROLE_ANONYMOUS, ROLE_USER, TEAM_MEMBER] }], {
      shouldDirty: true,
      shouldValidate: true
    })
  }

  const remove = (index: number) => {
    if (invitations.length <= 1) return
    const next = invitations.filter((_, i) => i !== index)
    form.setValue("invitations", next, {
      shouldDirty: true,
      shouldValidate: true
    })
  }

  return (
    <>
      <div className="space-y-3 max-h-[60vh] overflow-y-auto">
        {invitations.map((_, index) => (
          <div
            key={index}
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

            <div className="flex flex-col pl-1">
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
              {invitations.length > 1 && (
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
        className="mt-1 w-[185px]"
        onClick={append}
      >
        <IconPlus className="mr-2 size-4" />
        Add another
      </Button>
    </>
  )
}
