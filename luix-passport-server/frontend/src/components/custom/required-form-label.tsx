import { FormLabel } from "@/components/ui/form"

type Props = {
  children: string
  required?: boolean
}

export const RequiredFormLabel = ({
  children,
  required = false
}: Props) => (
  <FormLabel>
    {children}
    {required && <span className="ml-1 text-destructive font-bold align-middle">*</span>}
  </FormLabel>
)