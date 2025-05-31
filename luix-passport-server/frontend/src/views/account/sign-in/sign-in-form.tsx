import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { PasswordInput } from "@/components/custom/password-input";
import { LoadingButton } from "@/components/custom/loading-button"
import { Link } from 'react-router-dom'

const formSchema = z.object({
  username: z.string().min(1, { message: "Please enter your username" }),
  password: z.string().min(1, { message: "Please enter your password" }),
});

export function SignInForm() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [logoutMessage, setLogoutMessage] = useState<string | null>(null);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  async function onSubmit(data: z.infer<typeof formSchema>) {
    setIsLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append('username', data.username);
      formData.append('password', data.password);

      const response = await fetch('/login', {
        method: 'POST',
        body: formData,
        headers: {
          // This header is important for server-side frameworks to recognize form data
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Invalid username or password');
      }

      // Handle successful login (e.g., redirect)
      window.location.href = '/dashboard';
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Login failed');
    } finally {
      setIsLoading(false);
    }
  }

  // Check URL for the logout parameter (simulating Thymeleaf's th:if="${param.logout}")
  if (typeof window !== 'undefined') {
    const params = new URLSearchParams(window.location.search);
    if (params.get('logout')) {
      setLogoutMessage('You have been logged out');
    }
  }

  return (
    <div className="w-full">
      {/* GitHub OAuth Button */}
        <form action="/oauth2/authorization/github-idp" method="GET">
          <Button
            type="submit"
            variant="outline"
            className="w-full py-4 h-14 mb-6 rounded-2xl"
          >
            <svg
              className="h-5 w-5 mr-2"
              viewBox="0 0 2350 2314.8"
            >
              <path
                fill="currentColor"
                d="M1175,0C525.8,0,0,525.8,0,1175c0,552.2,378.9,1010.5,890.1,1139.7c-5.9-14.7-8.8-35.3-8.8-55.8v-199.8H734.4
              c-79.3,0-152.8-35.2-185.1-99.9c-38.2-70.5-44.1-179.2-141-246.8c-29.4-23.5-5.9-47,26.4-44.1c61.7,17.6,111.6,58.8,158.6,120.4
              c47,61.7,67.6,76.4,155.7,76.4c41.1,0,105.7-2.9,164.5-11.8c32.3-82.3,88.1-155.7,155.7-190.9c-393.6-47-581.6-240.9-581.6-505.3
              c0-114.6,49.9-223.3,132.2-317.3c-26.4-91.1-61.7-279.1,11.8-352.5c176.3,0,282,114.6,308.4,143.9c88.1-29.4,185.1-47,284.9-47
              c102.8,0,196.8,17.6,284.9,47c26.4-29.4,132.2-143.9,308.4-143.9c70.5,70.5,38.2,261.4,8.8,352.5c82.3,91.1,129.3,202.7,129.3,317.3
              c0,264.4-185.1,458.3-575.7,499.4c108.7,55.8,185.1,214.4,185.1,331.9V2256c0,8.8-2.9,17.6-2.9,26.4
              C2021,2123.8,2350,1689.1,2350,1175C2350,525.8,1824.2,0,1175,0L1175,0z"
              />
            </svg>
            Sign in with GitHub
          </Button>
        </form>

        {/* Divider */}
        <div className="flex items-center mb-6">
          <hr className="h-0 border-b border-muted grow" />
          <p className="mx-4 text-sm text-muted-foreground uppercase">or continue with</p>
          <hr className="h-0 border-b border-muted grow" />
        </div>

        {/* Error messages */}
        {error && (
          <div className="mb-6 p-4 text-center text-destructive bg-destructive/10 rounded-lg">
            {error}
          </div>
        )}
        {logoutMessage && (
          <div className="mb-6 p-4 text-center text-muted-foreground">
            {logoutMessage}
          </div>
        )}

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="yourusername"
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <div className="flex items-center justify-between">
                    <FormLabel>Password</FormLabel>
                    <Link to="/forgot-password" className="text-sm font-medium text-blue-600 hover:underline">
                      Forgot password?
                    </Link>
                  </div>
                  <FormControl>
                    <PasswordInput
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <LoadingButton type="submit" loading={isLoading} className="w-full py-4 h-14 rounded-2xl">
              {isLoading ? "Waiting..." : "Sign In"}
            </LoadingButton>
          </form>
        </Form>
    </div>
  );
}