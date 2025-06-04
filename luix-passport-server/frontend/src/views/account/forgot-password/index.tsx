import { useState } from "react";
import { cn } from "@/lib/utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { LoadingButton } from "@/components/custom/loading-button";
import { Form } from "@/components/ui/form";
import { toast } from "sonner";
import InputFormField from "@/components/custom/form-field/input";
import { AccountService } from "@/services/account-service";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom"
import { getErrorMessage } from "@/lib/handle-error";

const formSchema = z.object({
  email: z
    .string()
    .min(1, { message: "Please enter your email" })
    .email({ message: "Invalid email address" }),
});

export default function ForgotPassword() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: { email: "" },
  });

  function requestReset(data: z.infer<typeof formSchema>) {
    setIsLoading(true);

    toast.promise(AccountService.requestPasswordRecovery(data.email), {
      loading: "Sending reset link...",
      success: () => {
        setTimeout(() => {
          navigate("/sign-in")
          setIsLoading(false);
        }, 5000);
        return "Reset link sent successfully. Please check your email.";
      },
      error: (error) => {
        setIsLoading(false);
        return getErrorMessage(error);
      }
    });
  }

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Reset Your Password
          </h2>
          <h4 className="mt-6 text-lg">
            Secure your account with our password recovery process
          </h4>
          <p className="text-sm mt-10">
            Remember your password?{" "}
            <Link to="/login" className="font-bold text-primary hover:underline">
              Sign in here
            </Link>
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold">Forgot Password</h1>
          <p className="text-muted-foreground mt-2">
            Enter your email to receive a reset link
          </p>
        </div>

        <div className={cn("grid gap-6")}>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(requestReset)} className="space-y-4">
              <InputFormField
                control={form.control}
                name="email"
                label="Email"
                formItemClassName="text-left"
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                placeholder="name@example.com"
              />

              <LoadingButton
                type="submit"
                loading={isLoading}
                className="w-full py-4 h-14 rounded-2xl"
              >
                {isLoading ? "Waiting 60s" : "Send Reset Link"}
              </LoadingButton>
            </form>
          </Form>
        </div>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          Don't have an account?{" "}
          <Link to="/sign-up" className="font-bold text-primary hover:underline">
            Sign up
          </Link>
        </p>

        <p className="mt-8 px-8 text-center text-sm text-muted-foreground">
          By continuing, you agree to our{" "}
          <Link to="/terms-of-service" className="underline hover:text-primary">
            Terms of Service
          </Link>{" "}
          and{" "}
          <Link to="/privacy-policy" className="underline hover:text-primary">
            Privacy Policy
          </Link>
        </p>
      </div>
    </div>
  );
}