import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { IconBook, IconCode, IconKey, IconShieldLock } from "@tabler/icons-react";

export default function AuthenticationPage() {
  const docSections = [
    {
      title: "Installation",
      description: "Set up the SDK in your project.",
      link: "/docs/installation",
      icon: <IconCode className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "Authentication",
      description: "Implement secure authentication flows.",
      link: "/docs/authentication",
      icon: <IconKey className="h-5 w-5 text-primary mr-2" />,
    },
    {
      title: "API Reference",
      description: "Explore our API endpoints.",
      link: "/docs/api-reference",
      icon: <IconBook className="h-5 w-5 text-primary mr-2" />,
    },
  ];

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-3 bg-white">
      {/* Left side - 1/3 width */}
      <div className="lg:flex relative hidden lg:col-span-1 lg:px-12 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-4xl text-3xl font-extrabold lg:leading-[50px]">
            Documentation
          </h2>
          <h4 className="mt-6 text-lg">
            Everything you need to build with our platform.
          </h4>
          <div className="mt-10">
            <Button
              asChild
              variant="outline"
              className="px-8 py-4 h-14 text-base rounded-2xl"
            >
              <Link to="/sign-up">Get Started</Link>
            </Button>
          </div>
          <nav className="mt-10">
            <ul className="space-y-4">
              {docSections.map((section, index) => (
                <li key={index}>
                  <Link
                    to={section.link}
                    className={`flex items-center text-base ${
                      section.title === "Authentication"
                        ? "text-primary font-semibold"
                        : "text-muted-foreground hover:text-primary"
                    }`}
                  >
                    {section.icon}
                    {section.title}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </div>
      </div>

      {/* Right side - 2/3 width */}
      <div className="flex flex-col w-full h-full lg:col-span-2 xl:px-32 lg:px-20 px-5 bg-white rounded-3xl py-10 overflow-y-auto">
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">Authentication</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Implement secure authentication flows for your application.
          </p>

          <div className="space-y-12">
            {/* Email/Password Authentication */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconKey className="h-5 w-5 text-primary mr-2" />
                Email/Password Authentication
              </h3>
              <p className="text-muted-foreground mb-4">
                Set up email and password login for your users.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`import Luix from '@luix-universe/sdk';

const luix = new Luix({ apiKey: 'your-api-key' });

async function signUp(email, password) {
  try {
    const user = await luix.auth.signUp({ email, password });
    return user;
  } catch (error) {
    console.error('Sign-up failed:', error);
  }
}

async function signIn(email, password) {
  try {
    const user = await luix.auth.signIn({ email, password });
    return user;
  } catch (error) {
    console.error('Sign-in failed:', error);
  }
}
`}
                </pre>
              </div>
            </div>

            {/* Social Logins */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconKey className="h-5 w-5 text-primary mr-2" />
                Social Logins
              </h3>
              <p className="text-muted-foreground mb-4">
                Enable login with Google, GitHub, or other providers.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`import Luix from '@luix-universe/sdk';

const luix = new Luix({ apiKey: 'your-api-key' });

async function signInWithGoogle() {
  try {
    const user = await luix.auth.signInWithProvider('google');
    return user;
  } catch (error) {
    console.error('Google sign-in failed:', error);
  }
}

async function signInWithGitHub() {
  try {
    const user = await luix.auth.signInWithProvider('github');
    return user;
  } catch (error) {
    console.error('GitHub sign-in failed:', error);
  }
}
`}
                </pre>
              </div>
            </div>

            {/* Multi-Factor Authentication */}
            <div className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
              <h3 className="text-xl font-semibold mb-3 flex items-center">
                <IconShieldLock className="h-5 w-5 text-primary mr-2" />
                Multi-Factor Authentication
              </h3>
              <p className="text-muted-foreground mb-4">
                Add an extra layer of security with MFA.
              </p>
              <div className="bg-muted/50 p-4 rounded-lg">
                <pre className="text-sm text-muted-foreground">
{`import Luix from '@luix-universe/sdk';

const luix = new Luix({ apiKey: 'your-api-key' });

async function enableMFA(userId) {
  try {
    await luix.auth.enableMFA(userId, { method: 'sms' });
    console.log('MFA enabled');
  } catch (error) {
    console.error('MFA setup failed:', error);
  }
}

async function verifyMFA(userId, code) {
  try {
    const verified = await luix.auth.verifyMFA(userId, code);
    return verified;
  } catch (error) {
    console.error('MFA verification failed:', error);
  }
}
`}
                </pre>
              </div>
            </div>
          </div>

          <div className="mt-16 border rounded-2xl p-6 bg-muted/50">
            <h3 className="text-xl font-semibold mb-4">Need Help?</h3>
            <div className="space-y-6">
              <div>
                <h4 className="font-medium">Community Support</h4>
                <p className="text-muted-foreground mt-1">
                  Join our developer community on Discord or GitHub to ask questions and share ideas.
                </p>
              </div>
              <div>
                <h4 className="font-medium">Enterprise Support</h4>
                <p className="text-muted-foreground mt-1">
                  Get dedicated support with our Enterprise plan, including a personal account manager.
                </p>
              </div>
              <div>
                <h4 className="font-medium">Authentication Guides</h4>
                <p className="text-muted-foreground mt-1">
                  Explore our detailed guides for advanced authentication setups.
                </p>
                <Button
                  asChild
                  variant="outline"
                  className="mt-2 px-4 py-2 h-10 text-sm rounded-2xl"
                >
                  <Link to="/docs/auth-guides">View Guides</Link>
                </Button>
              </div>
            </div>
          </div>
        </div>

        <footer className="mt-16 pt-10 border-t">
          <div className="flex justify-between items-center">
            <p className="text-sm text-muted-foreground">
              © 2025 LUIX Universe. All rights reserved.
            </p>
            <div className="flex space-x-6">
              <Link
                to="/terms-of-service"
                className="text-sm text-muted-foreground hover:text-primary"
              >
                Terms
              </Link>
              <Link
                to="/privacy-policy"
                className="text-sm text-muted-foreground hover:text-primary"
              >
                Privacy
              </Link>
              <Link
                to="/contact-us"
                className="text-sm text-muted-foreground hover:text-primary"
              >
                Contact
              </Link>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}