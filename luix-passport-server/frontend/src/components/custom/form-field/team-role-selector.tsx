import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuCheckboxItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { ChevronDown } from "lucide-react"
import {
  ROLE_ANONYMOUS,
  ROLE_USER,
  TEAM_MEMBER,
  TEAM_OWNER,
  SYSTEM_ROLES,
  TEAM_ROLES
} from "@/permission/role-control"
import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"

// ----- TeamRoleSelector Component -----

interface TeamRoleSelectorProps {
  value: string[]
  onChange: (roles: string[]) => void
  allRoles: string[]
  disabled?: boolean
  className?: string
  align?: "start" | "end" | "center"
}

export function TeamRoleSelector({
  value = [],
  onChange,
  allRoles,
  disabled = false,
  className,
  align = "start",
}: TeamRoleSelectorProps) {
  const handleRoleChange = (role: string, checked: boolean) => {
    let newRoles = [...value]
    if (checked) {
      if (!newRoles.includes(role)) newRoles.push(role)
    } else {
      newRoles = newRoles.filter(r => r !== role)
    }

    // Ensure mandatory roles
    if (!newRoles.includes(ROLE_ANONYMOUS)) newRoles.push(ROLE_ANONYMOUS)
    if (!newRoles.includes(ROLE_USER)) newRoles.push(ROLE_USER)

    // Mutual exclusion logic
    const hasMember = newRoles.includes(TEAM_MEMBER)
    const hasOwner = newRoles.includes(TEAM_OWNER)

    if (hasMember && hasOwner) {
      const wasOwner = value.includes(TEAM_OWNER)
      if (wasOwner) {
        // Was owner, now both -> Remove OWNER (Switch to Member)
        const index = newRoles.indexOf(TEAM_OWNER)
        if (index > -1) newRoles.splice(index, 1)
      } else {
        // Was member (or neither), now both -> Remove MEMBER (Switch to Owner)
        const index = newRoles.indexOf(TEAM_MEMBER)
        if (index > -1) newRoles.splice(index, 1)
      }
    } else if (!hasMember && !hasOwner) {
      // Neither selected -> Re-add the one that was there
      const wasOwner = value.includes(TEAM_OWNER)
      if (wasOwner) newRoles.push(TEAM_OWNER)
      else newRoles.push(TEAM_MEMBER)
    }

    onChange(newRoles)
  }

  const systemRoles = allRoles.filter(r => SYSTEM_ROLES.includes(r))
  const teamRoles = allRoles.filter(r => TEAM_ROLES.includes(r))
  const customRoles = allRoles.filter(r => !SYSTEM_ROLES.includes(r) && !TEAM_ROLES.includes(r))

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          size="sm"
          className={`h-9 justify-between font-normal ${className}`}
          disabled={disabled}
        >
          <span className="truncate">
            {/* Show meaningful roles first? Or just all joined.  */}
            {value.length > 0 ? value.join(", ") : "Select roles"}
          </span>
          <ChevronDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-56" align={align}>

        {systemRoles.length > 0 && (
          <>
            <DropdownMenuLabel>System Roles (Mandatory)</DropdownMenuLabel>
            {systemRoles.map((role) => (
              <DropdownMenuCheckboxItem
                key={role}
                checked={value.includes(role)}
                disabled
                onSelect={(e) => e.preventDefault()}
              >
                {role}
              </DropdownMenuCheckboxItem>
            ))}
            <DropdownMenuSeparator />
          </>
        )}

        {teamRoles.length > 0 && (
          <>
            <DropdownMenuLabel>Team Access (One Required)</DropdownMenuLabel>
            {teamRoles.map((role) => (
              <DropdownMenuCheckboxItem
                key={role}
                checked={value.includes(role)}
                onSelect={(e) => e.preventDefault()}
                onCheckedChange={(checked) => handleRoleChange(role, checked)}
              >
                {role}
              </DropdownMenuCheckboxItem>
            ))}
            <DropdownMenuSeparator />
          </>
        )}

        {customRoles.length > 0 && (
          <>
            <DropdownMenuLabel>Custom Roles</DropdownMenuLabel>
            {customRoles.map((role) => (
              <DropdownMenuCheckboxItem
                key={role}
                checked={value.includes(role)}
                onSelect={(e) => e.preventDefault()}
                onCheckedChange={(checked) => handleRoleChange(role, checked)}
              >
                {role}
              </DropdownMenuCheckboxItem>
            ))}
          </>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

// ----- TeamRoleSelectorFormField Component -----

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label: string
  allRoles: string[]
  required?: boolean
  disabled?: boolean
  description?: string
  className?: string
  hide?: boolean
}

const TeamRoleSelectorFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  label,
  allRoles,
  required,
  disabled,
  description,
  className,
  hide = false
}: Props<TFieldValues>) => {
  if (hide) return null
  return (
    <FormField
      control={disabled ? undefined : control}
      name={name}
      render={({ field }) => (
        <FormItem className={className}>
          <RequiredFormLabel required={required}>{label}</RequiredFormLabel>
          <FormControl>
            <TeamRoleSelector
              value={field.value || []}
              onChange={field.onChange}
              allRoles={allRoles}
              disabled={disabled}
              className="w-full"
            />
          </FormControl>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default TeamRoleSelectorFormField
