import { UseFormReturn } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectorFormField from "@/components/custom/form-field/multi-selector"
import { Option } from "@/components/custom/form-field/multi-selector"
import SwitchFormField from "@/components/custom/form-field/switch"
import MultiTextInputFormField from "@/components/custom/form-field/multi-text-input"
import { type Auth2Client } from "@/domains/auth2-client"

interface Auth2ClientFormFieldsProps {
  form: UseFormReturn<Auth2Client>
  id?: string | null
  authenticationMethodOptions: Option[]
  grantTypeOptions: Option[]
  scopeOptions: Option[]
}

export function Auth2ClientFormFields({
  form,
  id,
  authenticationMethodOptions,
  grantTypeOptions,
  scopeOptions
}: Auth2ClientFormFieldsProps) {
  return (
    <>
      <InputFormField
        control={form.control}
        name="clientId"
        label="Client ID"
        required
        disabled={!!id}
      />

      <InputFormField
        control={form.control}
        name="clientName"
        label="Client Name"
        required
      />

      <InputFormField
        control={form.control}
        name="rawClientSecret"
        label="Raw Client Secret"
        required
        description="Do not forget the secret."
        hide={!!id}
      />

      <MultiSelectorFormField
        control={form.control}
        name="clientAuthenticationMethods"
        label="Authentication Methods"
        required
        options={authenticationMethodOptions}
        multiple={true}
      />

      <MultiSelectorFormField
        control={form.control}
        name="authorizationGrantTypes"
        label="Authentication Grant Types"
        required
        options={grantTypeOptions}
        multiple={true}
      />

      <MultiTextInputFormField
        control={form.control}
        name="redirectUris"
        label="Redirect URIs"
        required
        placeholder="Enter an URI"
        description="Valid redirect URIs after login successfully."
        addButtonText="Add URI"
      />

      <MultiTextInputFormField
        control={form.control}
        name="postLogoutRedirectUris"
        label="Post Logout Redirect URIs"
        required
        placeholder="Enter an URI"
        addButtonText="Add URI"
      />

      <MultiSelectorFormField
        control={form.control}
        name="scopes"
        label="Scopes"
        required
        options={scopeOptions}
        multiple={true}
      />

      <SwitchFormField
        control={form.control}
        name="enabled"
        label="Enabled"
        description="Prevent login accesswhen disabled."
      />
    </>
  )
}
